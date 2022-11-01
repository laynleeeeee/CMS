stop tomcat
sudo /etc/init.d/tomcat7 stop
# Copy the new war file in /tmp/ with the name CBS.war to tomcat directory
sudo cp dist/cbs.war /var/lib/tomcat7/webapps/cbs.war
# delete the old war file
sudo rm -r /var/lib/tomcat6/webapps/cbs
# restart tomcat
sudo /etc/init.d/tomcat7 start
