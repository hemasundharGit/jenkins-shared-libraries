def call(){
    dependencyCheck(
        additionalArguments: "--scan ./ --format XML --nvdApiKey ${env.NVD_API_KEY}",
        odcInstallation: 'OWASP'
    )

    dependencyCheckPublisher(
        pattern: '**/dependency-check-report.xml'
    )
}
