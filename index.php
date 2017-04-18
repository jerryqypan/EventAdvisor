<?php
  $dsn = 'mysql:host=cgi.cs.duke.edu;port=3306;dbname=qp7;';
  $username = 'qp7';
  $password = 'qnDM4.fo6sX_';
  try {
      $db = new PDO($dsn, $username, $password);
  } catch(PDOException $e) {
      die('Could not connect to the database:<br/>' . $e);
  }

  $longitude = floatval($_POST['longitude']);
  $latitude = floatval($_POST['latitude']);
  $sthandler = $db->prepare("SELECT * FROM Event where sqrt(pow('$longitude'-Event.longitude,2)+pow('$latitude'-Event.latitude,2))<.01");
  $sthandler->execute();
  $result = $sthandler->fetch(PDO::FETCH_ASSOC);
  echo $result['description']; //need to echo multiple results
    //mysqli_close($con);
   	// $text = $_POST['text'];
   	// echo '{"events":[{"title":"TestEvent","date":"4-31-2017","startTime":"12:00","endTime":"16:00","description":"This is a test event","longitude":35.998456,"latitude":-78.939116},{"title":"TestEvent2","date":"4-31-2017","startTime":"12:00","endTime":"16:00","description":"This is a test event","longitude":35.998456,"latitude":-78.939126}]}';
?>