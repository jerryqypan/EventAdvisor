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
  //$longitude = floatval($_POST['longitude']);
  //$latitude = floatval($_POST['latitude']);
  //$uid = $_POST['uid'];
  $uid = 'tztFki0vhfd7RjAvkjyKiK8VLxJ3';
  $longitude = -78.940294;
  $latitude = 36.001769;
  $date = date("Y/m/d");
  $sthandler = $db->prepare("SELECT e.idEvent,e.title,e.date,e.startTime,e.endTime,e.description,e.longitude,e.latitude,e.uid, IF(i.uid IS NULL,false,true) as 'isInterested' FROM Event e LEFT JOIN (select * from EventInterest z where z.uid=:uid) i on e.idEvent=i.idEvent where (sqrt(pow(:longitude-e.longitude,2)+pow(:latitude-e.latitude,2))<.01)"); //this prevents sql injections by parameterizing the inputs
  #$sthandler = $db->prepare("SELECT * FROM Event");
  $sthandler->execute(array(
    ':longitude' => $longitude,
    ':latitude' => $latitude,
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