<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>File Upload</title>
    <script>
        function validateFile() {
            var fileInput = document.getElementById('file');
            var filePath = fileInput.value;
            var allowedExtensions = /(\.txt|\.csv)$/i;
            if (!allowedExtensions.exec(filePath)) {
                alert('Please upload file having extensions .txt or .csv only.');
                fileInput.value = '';
                return false;
            }
            return true;
        }
    </script>
</head>
<body>
    <h2>File Upload</h2>
    <form method="post" enctype="multipart/form-data" action="/upload" onsubmit="return validateFile()">
        <input type="file" id="file" name="file"/>
        <button type="submit">Upload</button>
    </form>
    <form method="get"  action="/reset" action="reset">
        <button type="submit">reset</button>
    </form>
    <c:if test="${not empty message}">
        <div>${message}</div>
    </c:if>
    
   <c:if test="${not empty records}"> 
    
    <h2>Reports</h2>

    <h3>Total Number of Employees</h3>
    <p>${totalEmployees}</p>

<h1>Monthly Employee Join Details</h1>
    <c:forEach var="entry" items="${monthlyEmployeeJoinDetails}">
        <h4>${entry.key}</h4>
        <table border="">
            <thead>
                <tr>
                    <th>EmpID</th>
                    <th>EmpFName</th>
                    <th>EmpLName</th>
                    <th>Designation</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="record" items="${entry.value}">
                    <tr>
                        <td>${record.empID}</td>
                        <td>${record.empFName}</td>
                        <td>${record.empLName}</td>
                        <td>${record.designation}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:forEach>

   <h1>Monthly Employee Exit Details</h1>
<c:forEach var="entry" items="${monthlyEmployeeExitDetails}">
    <h4>Month: ${entry.key}</h4>
    <table border="1">
        <thead>
            <tr>
                <th>EmpID</th>
                <th>EmpFName</th>
                <th>EmpLName</th>
                <th>Designation</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="employeeDetail" items="${entry.value}">
                <tr>
                    <td>${employeeDetail['empID']}</td>
                    <td>${employeeDetail['empFName']}</td>
                    <td>${employeeDetail['empLName']}</td>
                    <td>${employeeDetail['designation']}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:forEach>
   <h1>Monthly Salary Report</h1>
    <table border="1">
        <tr>
            <th>Month</th>
            <th>Total Salary</th>
            <th>Total Employees</th>
        </tr>
        <c:forEach var="entry" items="${monthlySalaryReport}">
            <c:set var="month" value="${entry.key}" />
            <c:set var="data" value="${entry.value}" />
            <tr>
                <td>${month}</td>
                <td>${data['Total Salary']}</td>
                <td>${data['Total Employees']}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty monthlySalaryReport}">
            <tr>
                <td colspan="3">No data available</td>
            </tr>
        </c:if>
    </table>

    <h1>Employee Wise Financial Report</h1>
    <table border="1">
        <thead>
            <tr>
                <th>Employee Id</th>
                <th>Name</th>
                <th>Surname</th>
                <th>Total Amount Paid</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="entry" items="${employeeWiseFinancialReport}">
                <tr>
                    <td>${entry.key}</td>
                    <td>${entry.value.EmpFName}</td>
                    <td>${entry.value.EmpLName}</td>
                    <td>${entry.value.TotalAmountPaid}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

   <h1>Monthly Amount Released</h1>
    <table border="1">
        <tr>
            <th>Month</th>
            <th>Total Amount</th>
            <th>Total Employees</th>
        </tr>
        <c:forEach var="entry" items="${monthlyAmountReleased}">
            <c:set var="month" value="${entry.key}" />
            <c:set var="data" value="${entry.value}" />
            <tr>
                <td>${month}</td>
                <td>${data['Total Amount']}</td>
                <td>${data['Total Employees']}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty monthlyAmountReleased}">
            <tr>
                <td colspan="3">No data available</td>
            </tr>
        </c:if>
    </table>

    <h1>Yearly Financial Report</h1>
    <table border="1">
        <thead>
            <tr>
                <th>Event</th>
                <th>Emp Id</th>
                <th>Event Date</th>
                <th>Event Value</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="report" items="${yearlyFinancialReport}">
                <tr>
                    <td>${report.Event}</td>
                    <td>${report.EmpID}</td>
                    <td>${report.EventDate}</td>
                    <td>${report.EventValue}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
<h2>Uploaded Records</h2>
    <table border="1">
        <thead>
            <tr>
                <th>Sequence No</th>
                <th>EmpID</th>
                <th>EmpFName</th>
                <th>EmpLName</th>
                <th>Designation</th>
                <th>Event</th>
                <th>Value</th>
                <th>Event Date</th>
                <th>Notes</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="record" items="${records}"> 
                <tr>
                    <td>${record.sequenceNo}</td>
                    <td>${record.empID}</td>
                    <td>${record.empFName}</td>
                    <td>${record.empLName}</td>
                    <td>${record.designation}</td>
                    <td>${record.event}</td>
                    <td>${record.value}</td>
                    <td>${record.eventDate}</td>
                    <td>${record.notes}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

     </c:if>
    </body>
  </html>
