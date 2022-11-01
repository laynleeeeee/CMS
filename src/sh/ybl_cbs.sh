# 
# Shell script that creates a back up of the CBS database and push it to ybl.CMS.com:/ybl_live_back.

#

#!/bin/bash

# temporary folder that will be used in dumping the data. 
TMP_FOLDER_NAME=tmp

# File name. ybl_prod_mm_dd_yyyy_HH_MM_SS.sql.gz
# this is a compressed file. 
FILE_NAME=ybl_prod_`date +%m_%d_%y_%H_%M_%S`.sql.gz

# The server directory
SERVER_DIRECTORY=ybl_live_back_up

# Do the actual back up
echo "performing mysqldump"
/usr/bin/mysqldump -uuser -ppassword --routines  CBS | gzip > /$TMP_FOLDER_NAME/$FILE_NAME

echo "Sending data to backup server"
scp /$TMP_FOLDER_NAME/$FILE_NAME ubuntu@ybl.CMS.com:$SERVER_DIRECTORY/
echo "Successfully dumped the YBL data to ybl back up server"

echo "deleting the dumped file to this server."
rm /$TMP_FOLDER_NAME/$FILE_NAME
echo "successfully deleted the file."