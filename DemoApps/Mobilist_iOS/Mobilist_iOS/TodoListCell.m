//
//  TodoListCell.m
//  Mobilist_iOS
//
//  Created by Richard Wotzlaw on 23.04.13.
//  Copyright (c) 2013 TU Dresden. All rights reserved.
//

#import "TodoListCell.h"

@implementation TodoListCell

@synthesize listNameLabel;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
