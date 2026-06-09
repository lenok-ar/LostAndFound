param(
    [Parameter(Mandatory = $true)] [string] $ProjectId,
    [Parameter(Mandatory = $true)] [string] $AccessToken,
    [Parameter(Mandatory = $true)] [string] $FcmToken
)

$body = @{
    message = @{
        token = $FcmToken
        data = @{
            title = "Lost and Found"
            body = "Test data message"
            target_route = "feed"
        }
    }
} | ConvertTo-Json -Depth 6

$bodyFile = Join-Path $env:TEMP "lost-and-found-fcm-message.json"
[System.IO.File]::WriteAllText($bodyFile, $body, [System.Text.UTF8Encoding]::new($false))
curl.exe -X POST `
    -H "Authorization: Bearer $AccessToken" `
    -H "Content-Type: application/json; charset=utf-8" `
    --data-binary "@$bodyFile" `
    "https://fcm.googleapis.com/v1/projects/$ProjectId/messages:send"

Remove-Item $bodyFile
