provider "aws" {
  region = "ap-northeast-1"
  default_tags {
    tags = {
      ManagedBy = "Terraformer"
    }
  }
}

locals {
  claim_sub       = "token.actions.githubusercontent.com:sub"
  repository_name = "kiririmode/hobby"
}

data "aws_caller_identity" "current" {}

resource "aws_iam_role" "github_actions" {
  name               = "GitHubActionsRole"
  description        = "GitHub Actions"
  assume_role_policy = data.aws_iam_policy_document.assume_role_policy.json
  managed_policy_arns = [
    aws_iam_policy.backend_access.arn,
    aws_iam_policy.github_actions.arn,
    "arn:aws:iam::aws:policy/AWSBudgetsActionsWithAWSResourceControlAccess"
  ]
}

data "aws_iam_policy_document" "assume_role_policy" {
  statement {
    actions = [
      "sts:AssumeRoleWithWebIdentity",
      "sts:TagSession"
    ]

    # GitHub ActionsのOIDC Provider
    principals {
      type        = "Federated"
      identifiers = [aws_iam_openid_connect_provider.github.arn]
    }

    condition {
      test     = "StringLike"
      variable = local.claim_sub
      values   = ["repo:${local.repository_name}:*"]
    }
  }
}

resource "aws_iam_policy" "backend_access" {
  name_prefix = "TerraformBackendAccessPolicy"
  description = "TerraformのBackendアクセス用ポリシー"
  policy      = data.aws_iam_policy_document.backend_access.json

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_iam_policy" "github_actions" {
  name_prefix = "GitHubActionsPolicy"
  description = "GitHub ActionsをOIDC Providerと連携して実行するためのPolicy"
  policy      = data.aws_iam_policy_document.github_actions.json

  lifecycle {
    create_before_destroy = true
  }
}

data "aws_iam_policy_document" "github_actions" {
  statement {
    actions = [
      "iam:GetPolicy",
      "iam:GetOpenIDConnectProvider",
      "iam:GetPolicyVersion",
      "iam:GetRole",
      "iam:ListRolePolicies",
      "iam:ListAttachedRolePolicies"
    ]
    resources = [
      # TODO: Error: error reading IAM Role (GitHubActionsRole): AccessDenied: User: arn:aws:sts::***:assumed-role/GitHubActionsRole/GitHubActions is not authorized to perform: iam:GetRole on resource: role GitHubActionsRole を回避するためのアスタリスク。
      # IAM Access Analyzer をみて、権限は狭められるはず。
      "*",
      aws_iam_policy.backend_access.arn,
      aws_iam_openid_connect_provider.github.arn
    ]
  }
}

# see: https://www.terraform.io/docs/language/settings/backends/s3.html
data "aws_iam_policy_document" "backend_access" {
  statement {
    actions   = ["s3:ListBucket"]
    resources = ["arn:aws:s3:::kiririmode-tfbackend"]
  }
  statement {
    actions = [
      "s3:GetObject",
      "s3:PutObject"
    ]
    resources = ["arn:aws:s3:::kiririmode-tfbackend/*"]
  }

  statement {
    actions = [
      "dynamodb:GetItem",
      "dynamodb:PutItem",
      "dynamodb:DeleteItem"
    ]
    resources = [
      "arn:aws:dynamodb:ap-northeast-1:629415618746:table/terraform_state"
    ]
  }
}
