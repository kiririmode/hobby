terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.64"
    }
  }
  required_version = "~> 1.0.10"

  backend "s3" {
    bucket         = "kiririmode-tfbackend"
    key            = "budgets"
    encrypt        = true
    dynamodb_table = "terraform_state"
  }
}
