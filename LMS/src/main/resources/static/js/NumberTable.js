/**
 *  To get the table of a number
 */

function sendInfo()
{  
	var number=document.getElementById("EnterNumber").value;  
	var url="http://localhost:8080/LibraryManagementSystem/GetTable?number="+number;  
	  
	const xhr = new XMLHttpRequest();
	xhr.open('GET', url, true);
	
	xhr.onreadystatechange = function(){
		if(xhr.readyState==4)
		{  
		var val=xhr.responseText;  
		document.getElementById('writeTable').innerHTML=val;  
		}  
	}
	xhr.send();
}