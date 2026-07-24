$ErrorActionPreference = "Continue"
$base = "http://localhost:8080"
$results = @()

function Test-Api {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [hashtable]$Headers = @{},
        [string]$Body = $null,
        [string]$ContentType = $null,
        [scriptblock]$Form = $null,
        [int[]]$ExpectedStatus = @(200)
    )

    $entry = [ordered]@{
        Name = $Name
        Method = $Method
        Url = $Url
        Status = "FAILED"
        HttpCode = $null
        Error = $null
        Body = $null
    }

    try {
        $params = @{
            Uri = $Url
            Method = $Method
            Headers = $Headers
            UseBasicParsing = $true
        }
        if ($Form) {
            $response = Invoke-WebRequest @params -Form $Form.Invoke()
        } elseif ($Body -ne $null) {
            $params.Body = $Body
            if ($ContentType) { $params.ContentType = $ContentType }
            $response = Invoke-WebRequest @params
        } else {
            $response = Invoke-WebRequest @params
        }
        $entry.HttpCode = [int]$response.StatusCode
        $entry.Body = if ($response.Content.Length -gt 300) { $response.Content.Substring(0, 300) + "..." } else { $response.Content }
        if ($ExpectedStatus -contains $entry.HttpCode) {
            $entry.Status = "WORKING"
        } else {
            $entry.Error = "Unexpected status code"
        }
    } catch {
        if ($_.Exception.Response) {
            $entry.HttpCode = [int]$_.Exception.Response.StatusCode
            try {
                $reader = [System.IO.StreamReader]::new($_.Exception.Response.GetResponseStream())
                $entry.Body = $reader.ReadToEnd()
                $entry.Error = $entry.Body
            } catch {
                $entry.Error = $_.Exception.Message
            }
            if ($ExpectedStatus -contains $entry.HttpCode) {
                $entry.Status = "WORKING"
            }
        } else {
            $entry.Error = $_.Exception.Message
        }
    }

    $script:results += [pscustomobject]$entry
    Write-Host "[$($entry.Status)] $Method $Url -> $($entry.HttpCode)"
    return $entry
}

# Setup test users
$ts = Get-Date -Format "yyyyMMddHHmmss"
$userEmail = "user_$ts@test.com"
$userPass = "password123"
$adminEmail = "admin_$ts@test.com"
$adminPass = "adminpass123"

# 1. Register user
Test-Api -Name "Register User" -Method POST -Url "$base/api/auth/register" `
    -Body (@{name="Test User"; email=$userEmail; password=$userPass} | ConvertTo-Json) `
    -ContentType "application/json"

# 2. Register admin user
Test-Api -Name "Register Admin User" -Method POST -Url "$base/api/auth/register" `
    -Body (@{name="Admin User"; email=$adminEmail; password=$adminPass} | ConvertTo-Json) `
    -ContentType "application/json"

# 3. Login user (form-urlencoded)
$loginEntry = Test-Api -Name "Login User" -Method POST -Url "$base/api/auth/login" `
    -Body "email=$userEmail&password=$userPass" `
    -ContentType "application/x-www-form-urlencoded"
$userToken = $loginEntry.Body.Trim('"')

# 4. Login with JSON (expected to fail - wrong format)
Test-Api -Name "Login User JSON (wrong format)" -Method POST -Url "$base/api/auth/login" `
    -Body (@{email=$userEmail; password=$userPass} | ConvertTo-Json) `
    -ContentType "application/json" -ExpectedStatus @(400,500)

$userHeaders = @{ Authorization = "Bearer $userToken" }

# 5. Get profile
Test-Api -Name "Get Profile" -Method GET -Url "$base/api/user/profile" -Headers $userHeaders

# 6. Update profile
Test-Api -Name "Update Profile" -Method PUT -Url "$base/api/user/profile?name=Updated+Name" -Headers $userHeaders

# 7. Change password
Test-Api -Name "Change Password" -Method PUT -Url "$base/api/user/change-password?oldPassword=$userPass&newPassword=newpass456" -Headers $userHeaders

# Re-login with new password
$loginEntry2 = Test-Api -Name "Re-login after password change" -Method POST -Url "$base/api/auth/login" `
    -Body "email=$userEmail&password=newpass456" `
    -ContentType "application/x-www-form-urlencoded"
$userToken = $loginEntry2.Body.Trim('"')
$userHeaders = @{ Authorization = "Bearer $userToken" }

# 8. Forgot password (no auth - test if public)
Test-Api -Name "Forgot Password (no auth)" -Method PUT -Url "$base/api/user/forgot-password?email=$userEmail&newPassword=resetpass789"

# 9. Dashboard
Test-Api -Name "User Dashboard" -Method GET -Url "$base/api/user/dashboard" -Headers $userHeaders

# 10. Recent resumes (empty)
Test-Api -Name "User Recent Resumes" -Method GET -Url "$base/api/user/recent-resumes" -Headers $userHeaders

# Create a test PDF file
$pdfPath = Join-Path $env:TEMP "test_resume.pdf"
"%PDF-1.4`n1 0 obj`n<<>>`nendobj`ntrailer`n<<>>`n%%EOF" | Out-File -FilePath $pdfPath -Encoding ascii

# 11. Upload resume
$uploadEntry = Test-Api -Name "Upload Resume" -Method POST -Url "$base/api/resumes/upload" -Headers $userHeaders -Form {
    @{
        name = "John Doe"
        phone = "9876543210"
        skills = "Java,Spring,MySQL"
        file = Get-Item $pdfPath
    }
}

# Extract resume ID from upload response
$resumeId = $null
try {
    $uploadJson = $uploadEntry.Body | ConvertFrom-Json
    $resumeId = $uploadJson.id
} catch {}

if (-not $resumeId) { $resumeId = 1 }

# 12. Get my resumes (3 paths)
Test-Api -Name "Get My Resumes (root)" -Method GET -Url "$base/api/resumes" -Headers $userHeaders
Test-Api -Name "Get My Resumes (/my-resumes)" -Method GET -Url "$base/api/resumes/my-resumes" -Headers $userHeaders

# 13. Search by name
Test-Api -Name "Search Resume by Name" -Method GET -Url "$base/api/resumes/search?name=John" -Headers $userHeaders

# 14. Search by skills
Test-Api -Name "Search Resume by Skills" -Method GET -Url "$base/api/resumes/search/skills?skills=Java" -Headers $userHeaders

# 15. Get resume by ID
Test-Api -Name "Get Resume By ID" -Method GET -Url "$base/api/resumes/$resumeId" -Headers $userHeaders

# 16. Preview resume
Test-Api -Name "Preview Resume" -Method GET -Url "$base/api/resumes/preview/$resumeId" -Headers $userHeaders

# 17. Download resume
Test-Api -Name "Download Resume" -Method GET -Url "$base/api/resumes/download/$resumeId" -Headers $userHeaders

# 18. Update resume
Test-Api -Name "Update Resume" -Method PUT -Url "$base/api/resumes/$resumeId" -Headers $userHeaders -Form {
    @{
        name = "John Updated"
        phone = "1112223333"
        skills = "Java,Spring Boot"
    }
}

# Admin tests - first login as admin user (need to promote via direct DB or existing admin)
# Login admin user
$adminLogin = Test-Api -Name "Login Admin User" -Method POST -Url "$base/api/auth/login" `
    -Body "email=$adminEmail&password=$adminPass" `
    -ContentType "application/x-www-form-urlencoded"
$adminToken = $adminLogin.Body.Trim('"')
$adminHeaders = @{ Authorization = "Bearer $adminToken" }

# 19. Admin stats (will fail if not ADMIN role)
Test-Api -Name "Admin Stats" -Method GET -Url "$base/api/admin/stats" -Headers $adminHeaders -ExpectedStatus @(200,403)

# Try to promote admin user using make-admin - need existing admin first
# Check if any admin exists by trying admin endpoints with user token
Test-Api -Name "Admin Stats (user token - expect 403)" -Method GET -Url "$base/api/admin/stats" -Headers $userHeaders -ExpectedStatus @(403)

# Get all users as admin (403 expected for regular user)
Test-Api -Name "Admin Get Users (user token)" -Method GET -Url "$base/api/admin/users" -Headers $userHeaders -ExpectedStatus @(403)

# 20. Delete resume
Test-Api -Name "Delete Resume" -Method DELETE -Url "$base/api/resumes/$resumeId" -Headers $userHeaders

# Re-upload for admin tests
$uploadEntry2 = Test-Api -Name "Re-upload Resume for admin tests" -Method POST -Url "$base/api/resumes/upload" -Headers $userHeaders -Form {
    @{
        name = "Admin Test Resume"
        phone = "5556667777"
        skills = "Python,Django"
        file = Get-Item $pdfPath
    }
}
try {
    $resumeId2 = ($uploadEntry2.Body | ConvertFrom-Json).id
} catch { $resumeId2 = $resumeId }

# Try admin endpoints without admin role
Test-Api -Name "Admin Get All Resumes (user token)" -Method GET -Url "$base/api/admin/resumes?page=0&size=10" -Headers $userHeaders -ExpectedStatus @(403)
Test-Api -Name "Admin Search Resumes (user token)" -Method GET -Url "$base/api/admin/resumes/search?name=Admin&page=0&size=10" -Headers $userHeaders -ExpectedStatus @(403)
Test-Api -Name "Admin Search Skills (user token)" -Method GET -Url "$base/api/admin/resumes/search/skills?skills=Python&page=0&size=10" -Headers $userHeaders -ExpectedStatus @(403)
Test-Api -Name "Admin Download (user token)" -Method GET -Url "$base/api/admin/resumes/download/$resumeId2" -Headers $userHeaders -ExpectedStatus @(403)
Test-Api -Name "Admin Preview (user token)" -Method GET -Url "$base/api/admin/resumes/preview/$resumeId2" -Headers $userHeaders -ExpectedStatus @(403)
Test-Api -Name "Admin Recent Resumes (user token)" -Method GET -Url "$base/api/admin/recent-resumes" -Headers $userHeaders -ExpectedStatus @(403)
Test-Api -Name "Admin Delete User (user token)" -Method DELETE -Url "$base/api/admin/users/999" -Headers $userHeaders -ExpectedStatus @(403)
Test-Api -Name "Admin Make Admin (user token)" -Method PUT -Url "$base/api/admin/users/1/make-admin" -Headers $userHeaders -ExpectedStatus @(403)
Test-Api -Name "Admin Remove Admin (user token)" -Method PUT -Url "$base/api/admin/users/1/remove-admin" -Headers $userHeaders -ExpectedStatus @(403)

# No auth tests
Test-Api -Name "Profile without token" -Method GET -Url "$base/api/user/profile" -ExpectedStatus @(401,403)
Test-Api -Name "Upload without token" -Method POST -Url "$base/api/resumes/upload" -ExpectedStatus @(401,403)

Write-Host "`n=== SUMMARY ==="
$working = ($results | Where-Object { $_.Status -eq "WORKING" }).Count
$failed = ($results | Where-Object { $_.Status -eq "FAILED" }).Count
Write-Host "Total: $($results.Count) | Working: $working | Failed: $failed"

$results | ConvertTo-Json -Depth 5 | Out-File "C:\Users\Dell\OneDrive\Desktop\resumemanagement\test-results-initial.json"
Write-Host "Results saved to test-results-initial.json"

# Export key vars for admin testing
@{
    userEmail = $userEmail
    userToken = $userToken
    adminEmail = $adminEmail
    adminPass = $adminPass
    resumeId2 = $resumeId2
} | ConvertTo-Json | Out-File "C:\Users\Dell\OneDrive\Desktop\resumemanagement\test-vars.json"
