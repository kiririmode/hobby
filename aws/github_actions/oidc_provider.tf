# AWSにGitHubのOIDCをfederated Identityとして認めてもらう
resource "aws_iam_openid_connect_provider" "github" {
  # see: https://docs.github.com/en/actions/deployment/security-hardening-your-deployments/configuring-openid-connect-in-amazon-web-services
  url = "https://token.actions.githubusercontent.com"
  client_id_list = [
    "sts.amazonaws.com",
  ]

  # see: https://qiita.com/minamijoyo/items/eac99e4b1ca0926c4310
  thumbprint_list = [
    "6938fd4d98bab03faadb97b34396831e3780aea1"
  ]
}
