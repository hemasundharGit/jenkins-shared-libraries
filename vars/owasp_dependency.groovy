def call(){
    dependencyCheck(
        additionalArguments: '--scan ./ --noupdate --format XML',
        odcInstallation: 'OWASP'
    )
    
    dependencyCheckPublisher(
        pattern: '**/dependency-check-report.xml'
    )
}
