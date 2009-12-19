<?php
/**
 *	QualiPSo
 *	http://www.qualipso.org/
 *
 *	Programming Language: PHP
 *
 *	File: clientMailmanMessage.php
 *	Description: This class is the client of the web services for Mailman message
 *	Author: Maria Alejandra Trujillo Valencia
 *	E-Mail: alejandra.trujillo@atosorigin.com
 *	Creation date: 27-04-2009
 *	Last modification: 22-05-2009 by Maria Alejandra Trujillo Valencia
 *
*/
		$list_id = $_GET["list_id"];
		$archive_id = $_GET["archive_id"];
		$message_id = $_GET["message_id"];
		
         //** SE INCLUYE LA CLASE DE SOAP **//
          require("nusoap-0.7.3/lib/nusoap.php");
         //** SE CREA EL CLIENTE **//
         $cliente = new soapclient("http://localhost/QualiPSo/Mailman/WebServices/serverMailmanMessage.php?wsdl", "true");
         $proxy  = $cliente->getProxy();
         //** LLAMA AL METODO QUE SE NECESITA **//
         //$uri = $_SERVER["REQUEST_URI"];
		 
         $resultado = $proxy->Message((string)"lists/".$list_id."/archives/".$archive_id."/messages/".$message_id.".json");

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
