<?php
	require 'notification_sender.php';

	$raw_data = file_get_contents("php://input");
	$appointment_data = json_decode($raw_data, false);
	$title = $appointment_data[0]->title;
	$message = $appointment_data[0]->message;
	$token = $appointment_data[0]->token;

	$registration_ids = array();
	array_push($registration_ids, $token);

	if (sizeof($registration_ids)>0) {
		$fields = array('registration_ids'=>$registration_ids, 'data'=>array('title'=>$title, 'message'=>$message));

		sendNotification($fields);
	}
?>