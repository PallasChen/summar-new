<?php
$servername = "localhost";
$username = "test";
$password = "1234";
$dbname = "original";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
	die("Connection failed: " . $conn->connect_error);
} else {
	// echo "connect success";
}
// $sql = $_POST['sql']; //android將會傳值到query
// $sql = $_POST['sql'];
// $sql = stripslashes($sql);
$sql = 'Select * From original limit 5';
// $sql = 'INSERT INTO number ( account, password) VALUES (233, 1234)';
// echo "$sql";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
	// output data of each row
	while ($row = $result->fetch_assoc()) {
		// echo "id: " . $row["id"] . "<br>";
		$data[] = $row;
	}
	$json = json_encode($data, JSON_UNESCAPED_UNICODE); //把資料轉換為JSON資料.
	echo $json;
} else {
	echo "0 results";
}
$conn->close();
?>