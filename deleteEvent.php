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
  $idEvent = $_POST['idEvent'];
  $sthandler = $db->prepare("Delete from EventInterest e where idEvent=e.idEvent"); //this prevents sql injections by parameterizing the inputs
  #$sthandler = $db->prepare("SELECT * FROM Event");
  $sthandler->execute(array(
    ':idEvent' => $idEvent
    ));
  $sthandler = $db->prepare("Delete from Event e where idEvent=e.idEvent"); //this prevents sql injections by parameterizing the inputs
  $sthandler->execute(array(
    ':idEvent' => $idEvent
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