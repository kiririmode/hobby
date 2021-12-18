terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "3.70.0"
    }
  }
  required_version = "1.1.2"

  backend "s3" {
    bucket         = "kiririmode-tfbackend"
    key            = "budgets"
    encrypt        = true
    dynamodb_table = "terraform_state"
    region         = "ap-northeast-1"
  }
}
