<?php
require "mysql_connection.php";
$query = file_get_contents("php://input");
if (mysqli_connect_errno()) {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
  exit();
}

// Perform queries and print out affected rows
if (!mysqli_query($con,$query)) {
  echo(mysqli_error($con));
} else {
	echo(mysqli_affected_rows($con));
}
mysqli_close($con);
?>