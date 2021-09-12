pipeline {
    agent any
    
    tools {
        maven "Maven"
    }
    
    stages {
        stage ('Compile Stage') {
            steps {
                  bat 'mvn clean compile'
            }
        }
        stage ('Build Stage') {
            steps {
                
                    bat 'mvn clean install'
            }
        }
       stage ('Test Sample Stage') {
            steps {
                
                    bat 'mvn clean install -Denv=samples '
            }
        }
      
        
       
    }
   
     post {
            always {
                cucumber '**/cucumber.json'
            }
         }   
}
