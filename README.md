# autofileuploader
Auto File Uploader for Jahia

This module import files/folders automatically into Jahia if the files/folders are put in a specific folder.
Per default his folder is \digital-factory-data\files.
The location of this folder can be reconfigured in jahia.properties. Based from the jahia webapp path you can set the property *autoUploadFilePathBasedOnWebRoot* to the specific path on the server.
A default interval check every 30 seconds the folder for new files. This can be also overloaded in jahia.properties with property *fileUploadScanInterval* (value must be in milliseconds)
