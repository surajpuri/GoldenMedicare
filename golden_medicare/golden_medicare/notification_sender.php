<?php
function sendNotification($fields){
	$path_to_fcm = 'https://fcm.googleapis.com/fcm/send';
	$server_key = "AAAA2xzL-jw:APA91bEK4i3HMDmj0hjIgHEmK44aQX2wPYvbia2CXY36lC7G_PQgtPzt9YCZankeG497Q_5O-H2Uu6QONJxktI_vAy6J45kI7RSRzj1h-0pi_GTn1a4Xbzt7IM7yjoCMcOTT2Yp4au_9";

	$headers = array('Authorization:key=' .$server_key,'Content-Type:application/json');

	$payload = json_encode($fields);

	$curl_session = curl_init();
	curl_setopt($curl_session, CURLOPT_URL, $path_to_fcm);
	curl_setopt($curl_session, CURLOPT_POST, true);
	curl_setopt($curl_session, CURLOPT_HTTPHEADER, $headers);
	curl_setopt($curl_session, CURLOPT_RETURNTRANSFER, true);
	curl_setopt($curl_session, CURLOPT_SSL_VERIFYPEER, false);
	curl_setopt($curl_session, CURLOPT_IPRESOLVE,CURL_IPRESOLVE_V4);
	curl_setopt($curl_session, CURLOPT_POSTFIELDS, $payload);

	$result = curl_exec($curl_session);
	var_dump($result);


	curl_close($curl_session);
}
?>