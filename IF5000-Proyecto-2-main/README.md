# IF5000-Proyecto-2

Este proyecto consiste en una emulación de un RAID-5 usando sockets UDP en Java.
Para la arquitectura RAID-5 es necesario contar con un mínimo de 3 nodos y esta funciona de la siguiente manera:
Se tienen los nodos con sus respectivos espacios en disco, este espacio contendra los archivos que son enviados desde el máster,
los archivos consisten en partes codificadas de un archivo alojado en el máster. Uno de los nodos contendra la paridad del archivo
esto quiere decir que en caso de que un nodo con una parte del archivo llegase a fallar (solo es posible que falle un nodo
a la vez), entonces se podrá recuperar esa parte faltante mediante la paridad.

Para hacer uso del proyecto es necesario contar con NetBeans 8.2. Una vez contando con el IDE NetBeans, se procede a los pasos para
la apertura y ejecución del proyecto.

Pasos:

1. Descargar el proyecto del repositorio, este contendra 3 carpetas de proyectos que son necesarios para la emulación.
2. Al tener las carpetas de ControllerNode, Node y saSEARCH, se procede a abrirlas desde el IDE
3. Una vez abiertas, se ejecuta primero el proyecto de ControllerNode y luego el proyecto Node(Este se ejecuta según la cantidad de nodos
que se desea poseer), por último se ejecuta el saSEARCH.
4. En la ventana de ejecución del ControllerNode(La que posee inputs y dos botones), se coloca el nombre del archivo que se desea enviar a los nodos
y el formato sin el punto(ej: pdf, txt), luego se presiona el botón de enviar.
5. Desde el saSEARCH se puede buscar el archivo que anteriormente fue enviado y así recuperarlo.

Simulación de fallo y ver el archivo recuperado:

-Para simular un fallo solo basta cerrar la ventana del nodo que se desea que falle.
-El archivo recuperado se encuentra en la carpeta llamada "Disk-Controller", esta se encuentra junto a la carpeta del proyecto.
