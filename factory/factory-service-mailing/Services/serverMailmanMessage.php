<?php
include("nusoap-0.7.3/lib/nusoap.php");
$server = new soap_server;
$server->configureWSDL('Message', 'urn:mesagge');

$server->register('Message', array("list_name" => "xsd:string"), array('return' => 'xsd:Array'), '');

function Message($list_name)
{
		//This $url_head is to prove, is neccesary change it for url mailman adaptor of qualipso
         $url_head =  "http://qualipsoweb.eurodyn.com:8000/mailman/v2.1.5/";
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
