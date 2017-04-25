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
  $name = $_POST['name'];
  $email = $_POST['email'];
  // $uid = 'tztFki0vhfd7RjAvkjyKiK8VLxJ3';
  // $name = 'Jerry Pan';
  // $email = 'qp7@duke.edu';
  $sthandler = $db->prepare("INSERT IGNORE into EventUsers(uid,name,email) values(:uid,:name,:email)"); //this prevents sql injections by parameterizing the inputs
  $sthandler->execute(array(
    ':uid' => $uid,
    ':name' => $name,
    ':email' => $email
    ));


?>