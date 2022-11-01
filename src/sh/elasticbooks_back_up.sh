#!/bin/bash

# Description: Script for copying database back up from elasticbook.com to local

mkdir -p /home/${USER}/CMS_backup/

scp -i /home/${USER}/.ssh/saturn.pub -r ubuntu@theulap.com:/home/ubuntu/CBS_DATABASE_BACKUP/`date --date="yesterday" +%m_%d_%y` /home/${USER}/CMS_backup/

