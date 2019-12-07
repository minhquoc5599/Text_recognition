<?php

$user = $_POST['user'];
$pass = md5($_POST['pass']);
$email = $_POST['email'];
$phone = $_POST['phone'];

$connect = mysqli_connect("localhost","id11857398_root","123456","id11857398_ocr");
mysqli_query($connect,"SET NAME 'utf8");

$query = "INSERT INTO taikhoan VALUES(NULL ,'$user', '$phone', '$email', '$pass')";

if(mysqli_query($connect, $query)){
    echo "Success";
} else {
    echo "Fail: Tài khoản email đã tồn tại.";
}

?>