function Get-CleanTree {
    param (
        [string]$Path = ".",
        [string[]]$Exclude = @(
        ".idea", ".gradle", ".git", "build",
        ".kotlin", "out", ".DS_Store"
    ),
        [int]$Depth = 12,
        [string]$Prefix = ""
    )

    if ($Depth -lt 0) { return }

    $items = Get-ChildItem -Path $Path -Force |
            Where-Object { $_.Name -notin $Exclude } |
            Sort-Object { -not $_.PSIsContainer }, Name

    $count = $items.Count
    for ($i = 0; $i -lt $count; $i++) {
        $item = $items[$i]
        $isLast = ($i -eq $count - 1)

        $branch = if ($isLast) { "└── " } else { "├── " }
        Write-Output "$Prefix$branch$($item.Name)"

        if ($item.PSIsContainer) {
            $newPrefix = if ($isLast) { "$Prefix    " } else { "$Prefix│   " }
            Get-CleanTree `
                -Path $item.FullName `
                -Exclude $Exclude `
                -Depth ($Depth - 1) `
                -Prefix $newPrefix
        }
    }
}

Get-CleanTree | Out-File -Encoding UTF8 clean-tree.txt
