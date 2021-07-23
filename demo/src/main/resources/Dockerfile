#FROM mysql
#COPY script.sql /script.sql

#COPY docker-entrypoint.sh /usr/local/bin/
#RUN ln -s usr/local/bin/docker-entrypoint.sh /entrypoint.sh
#ENTRYPOINT ["docker-entrypoint.sh"]

#EXPOSE 3307/tcp
# CMD mysql --user=root --password distance_calculator < script.sql


FROM mysql:5.7.17

 # mysql working location
ENV WORK_PATH /usr/local/

 # Define the directory that will be automatically executed by the container
ENV AUTO_RUN_DIR /docker-entrypoint-initdb.d

 # SQL to initialize the database
ENV FILE_0 script.sql

 # Execute SQL
ENV INSTALL_DATA_SHELL docker-entrypoint.sh

COPY ./$FILE_0 $WORK_PATH/

 #Place the shell file to be executed in the /docker-entrypoint-initdb.d/ directory, the container will automatically execute the shell
COPY ./$INSTALL_DATA_SHELL $AUTO_RUN_DIR/

CMD mysql --user=root --password distance_calculator
#< script.sql

 #Add executable permissions to executable files
RUN chmod a+x $AUTO_RUN_DIR/$INSTALL_DATA_SHELL




 # Set the command to be executed when the container starts
#CMD ["sh", "/docker-entrypoint-initdb.d/import.sh"]