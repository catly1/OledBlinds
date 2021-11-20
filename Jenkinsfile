class Constants {

    static final String MASTER_BRANCH = 'master'

    static final String QA_BUILD = 'Debug'
    static final String RELEASE_BUILD = 'Release'

    static final String INTERNAL_TRACK = 'internal'
    static final String RELEASE_TRACK = 'release'
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

pipeline {
    agent { dockerfile true }
    environment {
        appName = 'OLED Blinds'

        KEY_PASSWORD = credentials('keyPass')
        KEY_ALIAS = credentials('keyAlias')
        KEYSTORE = credentials('keystore')
        STORE_PASSWORD = credentials('storePass')
        GITHUB_TOKEN = credentials('catly')
        
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
        stage('Build APK') {
            when { expression { return isDeployCandidate() } }
            steps {
                echo 'Building APK'
                script {
                    VARIANT = getBuildType()
                    sh './gradlew -PstorePass=${STORE_PASSWORD} -Pkeystore=${KEYSTORE} -Palias=${KEY_ALIAS} -PkeyPass=${KEY_PASSWORD} assemble${VARIANT}'
                }
            }
        }
        stage('Deploy to Github') {
            when { expression { return isDeployCandidate() } }
            steps {
                echo "Publishing on Github..."
                script {
                    tag = $(git describe --tags)

                    try {
                        CHANGELOG = readFile(file: 'CHANGELOG.txt')
                    } catch (err) {
                        echo "Issue reading CHANGELOG.txt file: ${err.localizedMessage}"
                        CHANGELOG = ''
                    }

                    release=$(curl -XPOST -H "Authorization:token $token" --data "{\"tag_name\": \"$tag\", \"target_commitish\": \"master\", \"name\": \"${TAG}\", \"body\": \"${CHANGELOG}\", \"draft\": false, \"prerelease\": true}" https://api.github.com/repos/catly1/OledBlinds/releases)
                    id=$(echo "$release" | sed -n -e 's/"id":\ \([0-9]\+\),/\1/p' | head -n 1 | sed 's/[[:blank:]]//g')

                    curl -XPOST -H "Authorization:token $token" -H "Content-Type:application/octet-stream" --data-binary @artifact.zip https://uploads.github.com/repos/catly1/OledBlinds/releases/$id/assets?name=artifact.zip
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
    }
}