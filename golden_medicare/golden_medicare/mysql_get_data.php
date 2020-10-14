<?php
require "mysql_connection.php";

$query = file_get_contents("php://input");
$message;
try{
	$result=$con->query($query);
	$finalValue = array();
	while ($row = $result->fetch_assoc()) {
		$finalValue[] = $row;
	}
	$message = json_encode($finalValue);
	echo($message);
} catch(Exception $e){
	echo ("Connection Failure");
} finally{
	mysqli_close($con);
}
?>