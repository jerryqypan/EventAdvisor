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
  $longitude = floatval($_POST['longitude']);
  $latitude = floatval($_POST['latitude']);
  $sthandler = $db->prepare("SELECT * FROM Event where sqrt(pow(:longitude-Event.longitude,2)+pow(:latitude-Event.latitude,2))<.01"); //this prevents sql injections by parameterizing the inputs
  #$sthandler = $db->prepare("SELECT * FROM Event");
  $sthandler->execute(array(
    ':longitude' => $longitude,
    ':latitude' => $latitude
    ));
  while($result = $sthandler->fetch(PDO::FETCH_ASSOC)){
    $json.=json_encode($result); //need to echo multiple results
    $json.=',';
  }
  $json = rtrim($json,',');
  $json.=']}';
  print_r($json);
?>