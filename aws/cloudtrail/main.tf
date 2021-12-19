provider "aws" {
  region = "ap-northeast-1"
  default_tags {
    tags = {
      ManagedBy = "Terraformer"
    }
  }
}

locals {
  bucket_name = "kiririmode-trail-bucket"
}

data "aws_caller_identity" "current" {}

resource "aws_cloudtrail" "this" {
  name           = "kiririmode-trail"
  s3_bucket_name = aws_s3_bucket.trail.id

  enable_logging                = true
  include_global_service_events = true
  is_multi_region_trail         = true
}

resource "aws_s3_bucket" "trail" {
  bucket        = local.bucket_name
  acl           = "private"
  force_destroy = false

  policy = data.aws_iam_policy_document.bucket_policy.json

  versioning {
    enabled = true
  }
  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm = "AES256"
      }
    }
  }
  object_lock_configuration {
    object_lock_enabled = "Enabled"
  }
  tags = {
    Name = "S3 Trail Bucket"
  }
}

# see: https://docs.aws.amazon.com/ja_jp/awscloudtrail/latest/userguide/create-s3-bucket-policy-for-cloudtrail.html
data "aws_iam_policy_document" "bucket_policy" {
  statement {
    actions = [
      "s3:GetBucketAcl"
    ]
    principals {
      type        = "Service"
      identifiers = ["cloudtrail.amazonaws.com"]
    }
    resources = ["arn:aws:s3:::${local.bucket_name}"]
  }

  statement {
    actions = [
      "s3:PutObject"
    ]
    principals {
      type        = "Service"
      identifiers = ["cloudtrail.amazonaws.com"]
    }
    resources = ["arn:aws:s3:::${local.bucket_name}/AWSLogs/${data.aws_caller_identity.current.account_id}/*"]
    condition {
      test     = "StringEquals"
      variable = "s3:x-amz-acl"
      values   = ["bucket-owner-full-control"]
    }
  }
}
