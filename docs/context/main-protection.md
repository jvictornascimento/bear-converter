# Main Protection

## Goal

The `main` branch must only receive code after the project checks pass.

## Required GitHub checks

When branch protection is configured in GitHub, mark these checks as required:

- `Tests`
- `Qodana`

The `Tests` workflow runs the Maven test suite.

The `Qodana` workflow runs JetBrains Qodana with the JVM Community linter.

## Qodana configuration

Qodana is configured in:

```text
qodana.yaml
```

The GitHub workflow uses:

```text
JetBrains/qodana-action@v2025.3
```

The quality gate currently uses:

```text
--fail-threshold 0
```

That means a pull request fails when Qodana finds a new problem.

To connect Qodana Cloud, configure this repository secret in GitHub:

Secrets:

- `QODANA_TOKEN`

## Pull request rule

Recommended branch protection for `main`:

- require pull request before merging;
- require status checks before merging;
- require `Tests`;
- require `Qodana`;
- require review approval;
- block direct pushes to `main`.
