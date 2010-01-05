<?php
/**
 *	QualiPSo
 *	http://www.qualipso.org/
 *
 *	Programming Language: PHP
 *
 *	File: clientMailmanMessages.php
 *	Description: This class is the client of the web services for Mailman messages
 *	Author: Maria Alejandra Trujillo Valencia
 *	E-Mail: alejandra.trujillo@atosorigin.com
 *	Creation date: 27-04-2009
 *	Last modification: 22-05-2009 by Maria Alejandra Trujillo Valencia
 *
*/
		$type = $_GET["type"];
		$list_id = $_GET["list_id"];
		$archive_id = $_GET["archive_id"];
         //** SE INCLUYE LA CLASE DE SOAP **//
          require("nusoap-0.7.3/lib/nusoap.php");
         //** SE CREA EL CLIENTE **//
		 $url_server = "http://ezwebdev.eurodyn.com/mailing-services/serverMailmanMessages.php";
         $cliente = new soapclient("".$url_server."?wsdl", "true");
         $proxy  = $cliente->getProxy();
         //** LLAMA AL METODO QUE SE NECESITA **//
         $uri = $_SERVER["REQUEST_URI"];
		 
        if($type == 1)
         {
             $resultado = $proxy->Messages((string)"lists/".$list_id."/archives/".$archive_id.".json");
         }
         else
         {
              $resultado = $proxy->Messages((string)"lists/".$list_id."/archives/".$archive_id."/messages.json");
         }
         //** SE REVISAN ERRORES **//
         if (!$cliente->getError())
         {
              print_r($resultado);
         }
         else
         {
             echo "<h1>Error: ".$cliente->getError()."</h1>";
         }
?>
