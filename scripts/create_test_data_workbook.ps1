$projectRoot = Split-Path -Parent $PSScriptRoot
$outputPath = Join-Path $projectRoot 'test-data/manual_test_cases.xlsx'
$tempDir = Join-Path $env:TEMP ('excel-workbook-' + [Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $tempDir -Force | Out-Null

$xlDir = Join-Path $tempDir 'xl'
$worksheetsDir = Join-Path $xlDir 'worksheets'
$relsDir = Join-Path $xlDir '_rels'
$rootRelsDir = Join-Path $tempDir '_rels'
New-Item -ItemType Directory -Path $worksheetsDir -Force | Out-Null
New-Item -ItemType Directory -Path $relsDir -Force | Out-Null
New-Item -ItemType Directory -Path $rootRelsDir -Force | Out-Null

function Write-XmlFile([string]$path, [string]$content) {
    [System.IO.File]::WriteAllText($path, $content, [System.Text.UTF8Encoding]::new($false))
}

function New-SheetXml([object[]]$rows) {
    $sheetData = New-Object System.Collections.Generic.List[string]
    for ($i = 0; $i -lt $rows.Count; $i++) {
        $rowCells = New-Object System.Collections.Generic.List[string]
        for ($j = 0; $j -lt $rows[$i].Count; $j++) {
            $value = [string]$rows[$i][$j]
            $cellRef = [string]([char]([int][char]'A' + $j)) + ($i + 1)
            $escaped = $value -replace '&', '&amp;' -replace '<', '&lt;' -replace '>', '&gt;'
            $rowCells.Add('<c r="' + $cellRef + '" t="inlineStr"><is><t>' + $escaped + '</t></is></c>')
        }
        $sheetData.Add('<row r="' + ($i + 1) + '">' + ($rowCells -join '') + '</row>')
    }
    return '<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><sheetData>' + ($sheetData -join '') + '</sheetData></worksheet>'
}

$manualHeaders = @('TC ID','Title','Preconditions','Test Steps','Test Data','Expected Result','Priority','Automation Status')
$manualCases = @(
    @('TC-001','Verify form submission with valid input','The demo app is accessible.','1. Open the app. 2. Enter valid first name, last name, email, and age. 3. Select a country and skills. 4. Tick the checkboxes. 5. Upload a valid image. 6. Click Submit.','FirstName=Ravi; LastName=Magar; Email=ravi@example.com; Age=34; Country=India; Skills=Java,Selenium','The form is submitted successfully and the result shows Submitted.','High','Automated'),
    @('TC-002','Verify dropdown and multi-select behavior','The demo app is loaded with the form visible.','1. Open the app. 2. Select a country from the dropdown. 3. Choose multiple skills from the multi-select list. 4. Submit the form.','Country=USA; Skills=Python,Selenium','The selected country and all chosen skills are reflected in the submission result.','High','Automated'),
    @('TC-003','Verify checkboxes stay unchecked until submission','The app is open and the form is displayed.','1. Open the app. 2. Select one checkbox only. 3. Observe the result message without clicking Submit.','Subscribe=true; Agree=false','The result text remains Ready until Submit is clicked.','Medium','Automated'),
    @('TC-004','Verify file upload field accepts a valid image','A sample image file is available in the test resources folder.','1. Open the app. 2. Select a valid image file using the upload control. 3. Observe the page state.','File=sample-image.png','The file is attached successfully and no upload error is shown.','High','Automated'),
    @('TC-005','Verify status button changes page output','The app is open and interactive controls are visible.','1. Open the app. 2. Click the Status button. 3. Observe the result message.','Button=Status','The result message updates to show the status action.','Medium','Automated'),
    @('TC-006','Verify form validation for empty mandatory fields','The app is open.','1. Leave required fields empty. 2. Click Submit. 3. Check the validation behavior.','All required fields empty','The user is prompted to complete the required fields before submission.','High','Manual'),
    @('TC-007','Verify multi-select retains multiple skill selections','The form is loaded with the skills dropdown available.','1. Open the app. 2. Select several skills from the multi-select control. 3. Submit the form.','Skills=Java;Python;Selenium','All selected skills remain visible in the form result.','Medium','Automated'),
    @('TC-008','Verify dropdown options are available','The app is open.','1. Open the app. 2. Expand the country dropdown. 3. Review the listed values.','Country options=India;USA;UK','The dropdown contains the expected options for selection.','Low','Manual'),
    @('TC-009','Verify file download flow','The download feature is enabled on the page.','1. Open the app. 2. Click the download control if present. 3. Verify the file is downloaded.','File type=image/png','The file is downloaded successfully to the expected location.','Medium','Manual'),
    @('TC-010','Verify submit button visibility and handling','The form is loaded in the browser.','1. Open the app. 2. Locate the Submit button. 3. Confirm it is visible and clickable.','Button=Submit','The Submit button is visible and responds to user action.','High','Automated')
)

$manualRows = @($manualHeaders) + $manualCases
$automationRows = @(
    @('FirstName','LastName','Email','Age','Country','Skills','Subscribe','Agree'),
    @('Ravi','Magar','ravi@example.com','34','India','Java, Selenium','true','true'),
    @('Asha','Patil','asha@example.com','29','USA','Python','false','false'),
    @('Kiran','Sharma','kiran@example.com','31','UK','Java, Python, Selenium','true','false'),
    @('Neha','Joshi','neha@example.com','27','India','Selenium','true','true')
)

Write-XmlFile -path (Join-Path $worksheetsDir 'sheet1.xml') -content (New-SheetXml $manualRows)
Write-XmlFile -path (Join-Path $worksheetsDir 'sheet2.xml') -content (New-SheetXml $automationRows)

Write-XmlFile -path (Join-Path $tempDir '[Content_Types].xml') -content @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/worksheets/sheet2.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
</Types>
'@

Write-XmlFile -path (Join-Path $rootRelsDir '.rels') -content @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
</Relationships>
'@

Write-XmlFile -path (Join-Path $relsDir 'workbook.xml.rels') -content @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet2.xml"/>
  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>
'@

Write-XmlFile -path (Join-Path $xlDir 'workbook.xml') -content @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets>
    <sheet name="Manual Test Cases" sheetId="1" r:id="rId1"/>
    <sheet name="Automation Data" sheetId="2" r:id="rId2"/>
  </sheets>
</workbook>
'@

Write-XmlFile -path (Join-Path $xlDir 'styles.xml') -content @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <fonts count="1"><font><sz val="11"/><name val="Calibri"/><family val="2"/></font></fonts>
  <fills count="1"><fill><patternFill patternType="none"/></fill></fills>
  <borders count="1"><border/></borders>
  <cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>
  <cellXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/></cellXfs>
  <cellStyles count="1"><cellStyle name="Normal" xfId="0" builtinId="0"/></cellStyles>
</styleSheet>
'@

if (Test-Path $outputPath) { Remove-Item $outputPath -Force }
try {
    [System.IO.Compression.ZipFile]::CreateFromDirectory($tempDir, $outputPath)
} catch {
    $zipPath = Join-Path $tempDir 'archive.zip'
    if (Test-Path $zipPath) { Remove-Item $zipPath -Force }
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    [System.IO.Compression.ZipFile]::CreateFromDirectory($tempDir, $zipPath)
    Copy-Item $zipPath $outputPath -Force
    Remove-Item $zipPath -Force
}
Remove-Item $tempDir -Recurse -Force
Write-Output $outputPath
