class Constants {

    static final String MASTER_BRANCH = 'master'

    static final String QA_BUILD = 'Debug'
    static final String RELEASE_BUILD = 'Release'

    static final String INTERNAL_TRACK = 'internal'
    static final String RELEASE_TRACK = 'production'
}

def getBuildType() {
    switch (env.BRANCH_NAME) {
        case Constants.MASTER_BRANCH:
            return Constants.RELEASE_BUILD
        default:
            return Constants.QA_BUILD
    }
}

def getTrackType() {
    switch (env.BRANCH_NAME) {
        case Constants.MASTER_BRANCH:
            return Constants.RELEASE_TRACK
        default:
            return Constants.INTERNAL_TRACK
    }
}

def isDeployCandidate() {
    return ("${env.BRANCH_NAME}" =~ /(develop|master)/)
}
def getReleaseInfo(String data) {
    def m = (data.replaceAll("\n"," ").trim() =~ /\{[^{]*"id": *([^,]*),.*/)
    if(!m) return null
    return m[0]
}

pipeline {
    agent { dockerfile true }
    environment {
        appName = 'OLED Blinds'

        KEY_PASSWORD = credentials('keyPass')
        KEY_ALIAS = credentials('keyAlias')
        KEYSTORE = credentials('keystore')
        STORE_PASSWORD = credentials('storePass')
        GITHUB_CREDS = credentials('catly')
        
    }
    stages {
        stage('Run Tests') {
            steps {
                echo 'Running Tests'
                script {
                    VARIANT = getBuildType()
                    sh "./gradlew test${VARIANT}UnitTest"
                }
            }
        }
        stage('Build Bundle') {
            when { expression { return isDeployCandidate() } }
            steps {
                echo 'Building AAB'
                script {
                    VARIANT = getBuildType()
                    sh './gradlew -PstorePass=${STORE_PASSWORD} -Pkeystore=${KEYSTORE} -Palias=${KEY_ALIAS} -PkeyPass=${KEY_PASSWORD} bundle${VARIANT}'
                }
            }
        }
        stage('Deploy App to Store') {
            when { expression { return isDeployCandidate() } }
            steps {
                echo 'Deploying'
                script {
                    VARIANT = getBuildType()
                    TRACK = getTrackType()

                    if (TRACK == Constants.RELEASE_TRACK) {
                        timeout(time: 5, unit: 'MINUTES') {
                            input "Proceed with deployment to ${TRACK}?"
                        }
                    }

                    try {
                        CHANGELOG = readFile(file: 'CHANGELOG.txt')
                    } catch (err) {
                        echo "Issue reading CHANGELOG.txt file: ${err.localizedMessage}"
                        CHANGELOG = ''
                    }

                    androidApkUpload googleCredentialsId: 'play-store-credentials',
                            filesPattern: "**/outputs/bundle/${VARIANT.toLowerCase()}/*.aab",
                            trackName: TRACK,
                            recentChangeList: [[language: 'en-US', text: CHANGELOG]],
                            rolloutPercentage: '100'
                }
            }
        }
        stage('Build APK') {
            when { expression { return isDeployCandidate() } }
            steps {
                echo 'Building APK'
                script {
                    sh './gradlew -PstorePass=${STORE_PASSWORD} -Pkeystore=${KEYSTORE} -Palias=${KEY_ALIAS} -PkeyPass=${KEY_PASSWORD} assembleRelease'
                    // archiveArtifacts '**/*.apk'
                }
            }
        }
        stage('Deploy to Github') {
            when { expression { return isDeployCandidate() } }
            steps {
                echo "Publishing on Github..."
                script {
                    def tag =  sh (
                        script: './gradlew -q printVersion',
                        returnStdout: true
                    ).trim()
                    echo "VersionInfo: ${tag}"
                    try {
                        CHANGELOG = readFile(file: 'CHANGELOG.txt')
                    } catch (err) {
                        echo "Issue reading CHANGELOG.txt file: ${err.localizedMessage}"
                        CHANGELOG = ''
                    }
                    sh "rm -rf RELEASE"
                    def API_create = "{\"tag_name\": \"${tag}\", \"target_commitish\": \"master\", \"name\": \"${tag}\", \"body\": \"${CHANGELOG}\", \"draft\": false, \"prerelease\": false}"
                    sh "curl -XPOST -H \"Authorization:token ${GITHUB_CREDS_PSW}\" --data '${API_create}' https://api.github.com/repos/catly1/OledBlinds/releases > RELEASE"
                    def release = readFile('RELEASE').trim()
                    def info = getReleaseInfo(release)
                    if(info != null) {
                        def release_id = info[1]
                        def location = "./app/build/outputs/apk/release/app-release.apk"
                        echo "file location ${location}"
                        sh "curl -XPOST -H \"Authorization:token ${GITHUB_CREDS_PSW}\" -H \"Content-Type:application/octet-stream\"  --data-binary @${location} https://uploads.github.com/repos/catly1/OledBlinds/releases/${release_id}/assets?name=app-release.apk"
                    }
                }
            }
        }
    }
}