locals {
  s3_access_log_lifecycle_rule_name = "s3log"
}

resource "aws_s3_bucket" "logs" {
  bucket = var.logs_bucket_name
  acl    = "private"

  force_destroy = true

  versioning {
    enabled = false
  }
  lifecycle_rule {
    id      = local.s3_access_log_lifecycle_rule_name
    enabled = true
    prefix  = var.s3_access_log_prefix

    tags = {
      rule = local.s3_access_log_lifecycle_rule_name
    }

    # ログは作成後1日で削除する
    expiration {
      days = 1
    }
  }

  tags = {
    Name = "Memories Logs"
  }
}
