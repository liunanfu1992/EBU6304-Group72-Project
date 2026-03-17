This repository contains our group project for the Spring 2026 offering of EBU6304 Software Processes, focused on designing and developing a TA recruitment system for BUPT International School. 

During the project initialization phase, team members should not push untested or unreviewed changes directly to the main branch, and should ensure that the `.gitignore` file is properly maintained to avoid polluting the repository.

## Example Workflow for Merging Work into `main`

Use a separate branch for your own work, then open a pull request to merge it into `main`.

```bash
git checkout main
git pull origin main
git checkout -b feature/update-readme

# make your changes
git add .
git commit -m "docs: update README"
git push -u origin feature/update-readme
```

After pushing your branch:

1. Open a Pull Request on GitHub from `feature/update-readme` to `main`.
2. Request at least one review from a teammate.
3. Merge the Pull Request only after the changes have been reviewed.

## Team Members

| GitHub Username | Name | QMUL ID |
| --- | --- | --- |
| liunanfu1992 | Jiayang Lyu | 231226130 |
| yztangyc | Yucheng Tang | 231226141 |
| giveyouupn | Qikun Hu | 231226255 |