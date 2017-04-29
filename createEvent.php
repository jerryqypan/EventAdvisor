<?php
  $dsn = 'mysql:host=cgi.cs.duke.edu;port=3306;dbname=qp7;';
  $username = 'qp7';
  $password = 'qnDM4.fo6sX_';
  try {
      $db = new PDO($dsn, $username, $password);
      //$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
  } catch(PDOException $e) {
      die('Could not connect to the database:<br/>' . $e);
  }
  $title = $_POST['title'];
  $date = $_POST['date'];
  $startTime = $_POST['startTime'];
  $endTime = $_POST['endTime'];
  $description = $_POST['description'];
  $longitude = floatval($_POST['longitude']);
  $latitude = floatval($_POST['latitude']);
  $uid = $_POST['uid'];
  $photo = $_POST['photo'];
  $binary=base64_decode($base);
  header('Content-Type: bitmap; charset=utf-8');
  $file = fopen('uploadedimages/test.png', 'wb');
  fwrite($file, $binary);
  fclose($file);
  // $title = "Wannamaker";
  // $date = "7-4-17";
  // $startTime = "14:00";
  // $endTime = "16:00";
  // $description = "Test wannamaker";
  // $longitude = 35.9990892;
  // $latitude = -78.9391148;
  $sthandler = $db->prepare("INSERT INTO Event(title,date,starttime,endtime,description,longitude,latitude,uid) values(:title,:date,:startTime,:endTime,:description,:longitude,:latitude,:uid)"); //this prevents sql injections by parameterizing the inputs
  #$sthandler = $db->prepare("SELECT * FROM Event");
  $sthandler->execute(array(
    ':title' => $title,
    ':date' => $date,
    ':startTime' => $startTime,
    ':endTime' => $endTime,
    ':description' => $description,
    ':longitude' => $longitude,
    ':latitude' => $latitude,
    ':uid' => $uid
    ));
    if($sthandler->errorCode() == 0) {
    while(($row = $sthandler->fetch()) != false) {
        echo $row . "\n";
    	}
	} else {
    $errors = $sthandler->errorInfo();
    echo($errors[2]);
}
?>