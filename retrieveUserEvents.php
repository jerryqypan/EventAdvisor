<?php
  $dsn = 'mysql:host=cgi.cs.duke.edu;port=3306;dbname=qp7;';
  $username = 'qp7';
  $password = 'qnDM4.fo6sX_';
  try {
      $db = new PDO($dsn, $username, $password);
  } catch(PDOException $e) {
      die('Could not connect to the database:<br/>' . $e);
  }
  $json = '{"events":[';
  //$uid = $_POST['uid'];
  $uid = 'tztFki0vhfd7RjAvkjyKiK8VLxJ3';
  $date = date("Y/m/d");
  $sthandler = $db->prepare("SELECT * FROM Event e where e.uid=:uid"); //this prevents sql injections by parameterizing the inputs
  #$sthandler = $db->prepare("SELECT * FROM Event");
  $sthandler->execute(array(
    ':uid' => $uid
    ));
  while($result = $sthandler->fetch(PDO::FETCH_ASSOC)){
    $json.=json_encode($result); //need to echo multiple results
    $json.=',';
  }
  $json = rtrim($json,',');
  $json.=']}';
  print_r($json);
?> 