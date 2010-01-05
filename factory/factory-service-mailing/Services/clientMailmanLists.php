<?php
/**
 *	QualiPSo
 *	http://www.qualipso.org/
 *
 *	Programming Language: PHP
 *
 *	File: clientMailmanLists.php
 *	Description: This class is the client of the web services for Mailman list
 *	Author: Maria Alejandra Trujillo Valencia
 *	E-Mail: alejandra.trujillo@atosorigin.com
 *	Creation date: 13-03-2009
 *	Last modification: 22-05-2009 by Maria Alejandra Trujillo Valencia
 *
*/	
		$type = $_GET["type"];
		$list_id = $_GET["list_id"];
		$archive_id = $_GET["archive_id"];
		
         //** SE INCLUYE LA CLASE DE SOAP **//
          require("nusoap-0.7.3/lib/nusoap.php");
         //** SE CREA EL CLIENTE **//
		 $url_server = "http://ezwebdev.eurodyn.com/mailing-services/serverMailmanLists"; 
         $cliente = new soapclient("".$url_server."?wsdl", "true");
         $proxy  = $cliente->getProxy();
         //** LLAMA AL METODO QUE SE NECESITA **//
        $uri = $_SERVER["REQUEST_URI"];
         $list = explode("?", $uri);
         $sizeList = sizeOf($list);

         if($sizeList > 1)
         {
             $resultado = $proxy->ListList((string)"lists/".$list[1]."/archives.json");
         }
         else
         {
              $resultado = $proxy->ListList((string)"lists.json");
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

