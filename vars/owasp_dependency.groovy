def call(){
    dependencyCheck(
        additionalArguments: '--scan ./ --format XML',
        odcInstallation: 'OWASP'
    )

    dependencyCheckPublisher(
        pattern: '**/dependency-check-report.xml'
    )
}
