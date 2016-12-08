#!/usr/bin/env bash

scp -r ./build sciar@users.itk.ppke.hu:/home/sciar/XPLanG
scp -r ./docs sciar@users.itk.ppke.hu:/home/sciar/XPLanG

ssh sciar@users.itk.ppke.hu "rm -rf /home/sciar/public_html/XPLanG/javadoc /home/sciar/public_html/XPLanG/reports /home/sciar/public_html/XPLanG/downloads/*.* && cp /home/sciar/XPLanG/build/distributions/* /home/sciar/public_html/XPLanG/downloads && cp -r /home/sciar/XPLanG/docs/javadoc /home/sciar/public_html/XPLanG && cp -r /home/sciar/XPLanG/build/reports /home/sciar/public_html/XPLanG && rm -rf /home/sciar/XPLanG/build && rm -rf /home/sciar/XPLanG/docs"
