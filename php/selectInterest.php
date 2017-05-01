<?php
  $dsn = 'mysql:host=cgi.cs.duke.edu;port=3306;dbname=qp7;';
  $username = 'qp7';
  $password = 'qnDM4.fo6sX_';
  try {
      $db = new PDO($dsn, $username, $password);
  } catch(PDOException $e) {
      die('Could not connect to the database:<br/>' . $e);
  }
  $uid = $_POST['uid'];
  $idEvent = $_POST['idEvent'];
  $addOrDelete = $_POST['addOrDelete'];
  if($addOrDelete == 'add'){
    $sthandler = $db->prepare("INSERT INTO EventInterest(idEvent,uid) values (:idEvent,:uid)");
  }else{
    $sthandler = $db->prepare("DELETE FROM EventInterest where idEvent=:idEvent and uid=:uid");
  }
  $sthandler->execute(array(
    ':idEvent' => $idEvent,
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