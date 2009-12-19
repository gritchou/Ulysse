<?php
include("nusoap-0.7.3/lib/nusoap.php");
$server = new soap_server;
$server->configureWSDL('Messages', 'urn:mesagges');

$server->register('Messages', array("list_name" => "xsd:string"), array('return' => 'xsd:Array'), '');

function Messages($list_name)
{
		//This $url_head is to prove, is neccesary change it for url mailman adaptor of qualipso
         $url_head =  "http://vulcano.morfeo-project.org/mailman/v1.1/remote/mailman.vulcano.morfeo-project.org/";
         $url = "".$url_head.$list_name."";
         $datos = fopen($url, "r");
         $contenido = fgets($datos);
         return $contenido;
}
    //** ENVIAR RESULTADO SOAP POR HTTP **/
    $rawPost = strcasecmp($_SERVER['REQUEST_METHOD'], "POST") == 0? (isset($GLOBALS['HTTP_RAW_POST_DATA'])? $GLOBALS['HTTP_RAW_POST_DATA'] : file_get_contents("php://input")) : NULL;
    $server->service($rawPost);
    exit();
?>
