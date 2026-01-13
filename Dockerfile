FROM tomcat:9-jdk17-temurin

# Copy WAR files to Tomcat webapps
COPY bitcoin/target/bitcoin-*.war $CATALINA_HOME/webapps/bitcoin.war
COPY influent-app/target/influent-app-*.war $CATALINA_HOME/webapps/influent-app.war
COPY kiva/target/kiva-*.war $CATALINA_HOME/webapps/kiva.war
COPY walker/target/walker-*.war $CATALINA_HOME/webapps/walker.war

# Set JVM options for large datasets
ENV CATALINA_OPTS="-Xmx10240m"

# Expose port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
