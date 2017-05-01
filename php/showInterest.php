<?php
  $dsn = 'mysql:host=cgi.cs.duke.edu;port=3306;dbname=qp7;';
  $username = 'qp7';
  $password = 'qnDM4.fo6sX_';
  try {
      $db = new PDO($dsn, $username, $password);
  } catch(PDOException $e) {
      die('Could not connect to the database:<br/>' . $e);
  }
  $uid = 'tztFki0vhfd7RjAvkjyKiK8VLxJ3';
  //$uid = $_POST['uid'];
  $json = '{"events":[';
  $sthandler = $db->prepare("SELECT Event.* FROM Event,EventInterest where (EventInterest.uid=:uid and Event.idEvent=EventInterest.idEvent) ");
  $sthandler->execute(array(
    ':uid' => $uid
    ));
  if($sthandler->errorCode() == 0) {
    while($result = $sthandler->fetch(PDO::FETCH_ASSOC)){
      $json.=json_encode($result); //need to echo multiple results
      $json.=',';
    }
    $json = rtrim($json,',');
    $json.=']}';
    print_r($json);
  } else {
    $errors = $sthandler->errorInfo();
    echo($errors[2]);
  }
?>