## APDU Protocol

This applet implements only the exact minimum of APDU commands that the NDEF specification prescribes.
All variants implement the same subset, limited only in implemented features.

##### **SELECT (CLA=00 INS=A4 P1=00 P2=0C CDATA=fid)**

    P1=00 means "SELECT BY FILEID"
    P2=0C means "SELECT FIRST OR ONLY"
       (Other selection modes are not supported.)
    
    Command eturns SW=9000 when successful.

   Select a file in the applet.

   In exception to ISO7816 no FCI (file control information) will be returned.
   This is permitted by NDEF specification requirement RQ_T4T_NDA_034.

   There are two files on the card:

     0xE103 - NDEF capabilities
     0xE104 - NDEF data

##### **READ BINARY (CLA=00 INS=B0 P12=offset RDATA=output)**

    P12 specifies the offset into the file and must be valid.
    
    Command returns SW=9000 when successful.

   Read data from the selected file.

   Length of RDATA is variable and depends on available resources, the protocol in use as well as the file size.
   As much data as possible will be returned.

##### **UPDATE BINARY (CLA=00 INS=D6 P12=offset CDATA=data)**

    P12 specifies the offset into the file and must be valid.
    
    Command returns SW=9000 when successful.

   Update data in the selected file.

   Allowable length of data depends on the build-time parameter NDEF_WRITE_SIZE (default is 128 bytes).
