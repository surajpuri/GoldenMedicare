<?php
	require "mysql_connection.php";
	require 'notification_sender.php';

	$raw_data = file_get_contents("php://input");
	$appointment_data = json_decode($raw_data, false);
	$appointment_id = $appointment_data[0]->appointment_id;
	$appointment_desc = $appointment_data[0]->appointment_desc;
	$category = $appointment_data[0]->category;
	$status = $appointment_data[0]->status;
	$user_name = $appointment_data[0]->user_name;
	$token = $appointment_data[0]->token;

	$registration_ids = array();

	$query = "SELECT user_token FROM tbl_user WHERE user_type = 1 LIMIT 1;";
	try{
		$result=$con->query($query);
		while ($row = $result->fetch_assoc()) {
			array_push($registration_ids, $row["user_token"]);
		}
	} catch(Exception $e){
	} finally{
		mysqli_close($con);
	}

	if (sizeof($registration_ids)>0) {
		$fields = array('registration_ids'=>$registration_ids, 'data'=>array('appointment_id'=>$appointment_id, 'appointment_desc'=>$appointment_desc, 'category'=>$category, 'user_name'=>$user_name));

		sendNotification($fields);
	}
?>