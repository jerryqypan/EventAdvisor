<?php
  $dsn = 'mysql:host=cgi.cs.duke.edu;port=3306;dbname=qp7;';
  $username = 'qp7';
  $password = 'qnDM4.fo6sX_';
  try {
      $db = new PDO($dsn, $username, $password);
  } catch(PDOException $e) {
      die('Could not connect to the database:<br/>' . $e);
  }
  $sthandler = $dbs->prepare("SELECT * FROM Event WHERE idEvent=1");
  $sthandler->execute();
  $result = $sthandler->fetch(PDO::FETCH_ASSOC);
  echo $result['title'];
    //mysqli_close($con);
   	// $text = $_POST['text'];
   	// echo '{"events":[{"title":"TestEvent","date":"4-31-2017","startTime":"12:00","endTime":"16:00","description":"This is a test event","longitude":35.998456,"latitude":-78.939116},{"title":"TestEvent2","date":"4-31-2017","startTime":"12:00","endTime":"16:00","description":"This is a test event","longitude":35.998456,"latitude":-78.939126}]}';
?>