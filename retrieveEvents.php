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
  #$longitude = -78.940294;
  #$latitude = 36.001769;
  $date = date("Y/m/d");
  $sthandler = $db->prepare("SELECT * FROM Event where (sqrt(pow(:longitude-Event.longitude,2)+pow(:latitude-Event.latitude,2))<.01) and (Event.date>=:date)"); //this prevents sql injections by parameterizing the inputs
  #$sthandler = $db->prepare("SELECT * FROM Event");
  $sthandler->execute(array(
    ':longitude' => $longitude,
    ':latitude' => $latitude,
    ':date' => $date
    ));
  while($result = $sthandler->fetch(PDO::FETCH_ASSOC)){
    $json.=json_encode($result); //need to echo multiple results
    $json.=',';
  }
  $json = rtrim($json,',');
  $json.=']}';
  print_r($json);
?>