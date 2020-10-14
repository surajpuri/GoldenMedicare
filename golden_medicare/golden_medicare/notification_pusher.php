<?php
	require 'notification_sender.php';

	$raw_data = file_get_contents("php://input");
	$report_data = json_decode($raw_data, false);
	$report_id = $report_data[0]->report_id;
	$report_title = $report_data[0]->report_title;
	$report_summary = $report_data[0]->report_summary;
	$report_remarks = $report_data[0]->report_remarks;
	$report_date = $report_data[0]->report_date;
	$token = $report_data[0]->token;
	$registration_ids = array();
	array_push($registration_ids, $token);

	$fields = array('registration_ids'=>$registration_ids, 'data'=>array('report_id'=>$report_id, 'report_title'=>$report_title,
	'report_summary'=>$report_summary, 'report_remarks'=>$report_remarks, 'report_date'=>$report_date));

	sendNotification($fields);

?>