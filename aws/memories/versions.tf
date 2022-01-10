terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.64"
    }
  }
  required_version = "1.1.2"

  backend "s3" {
    bucket         = "kiririmode-tfbackend"
    key            = "homemovie"
    encrypt        = true
    dynamodb_table = "terraform_state"
  }
}
