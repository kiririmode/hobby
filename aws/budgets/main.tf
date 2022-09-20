provider "aws" {
  region = "ap-northeast-1"
  default_tags {
    tags = {
      ManagedBy = "Terraformer"
    }
  }
}

locals {
  notification_emails = ["kiririmode@gmail.com"]
}

resource "aws_budgets_budget" "total" {
  # 予測コスト、実コストのそれぞれで設定
  for_each = toset(["FORECASTED", "ACTUAL"])

  name         = "total_budgets_${each.key}"
  budget_type  = "COST"
  limit_amount = "10"
  limit_unit   = "USD"
  time_unit    = "MONTHLY"

  cost_types {
    use_amortized = true
  }

  notification {
    comparison_operator        = "GREATER_THAN"
    threshold                  = 20
    threshold_type             = "PERCENTAGE"
    notification_type          = each.key
    subscriber_email_addresses = local.notification_emails
  }
}
